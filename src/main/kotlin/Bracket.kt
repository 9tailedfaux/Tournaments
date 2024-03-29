import kotlin.random.Random

class Bracket(val games: ArrayList<Game>, private val teams: ArrayList<Team>, val selectedGames: ArrayList<Game> = ArrayList()) {
    private val allRounds = ArrayList<Round>()
    private val losers = ArrayList<Team>()
    val finalRound: RootRound = RootRound(
        getGame(),
        1
    ).also { allRounds.add(it) }
    var currentRound: Round? = null

    fun generate() {
        var roundIndex = 2
        while(finalRound.getLeafCount() < (teams.size / 2) + (teams.size % 2)) {
            Round(roundIndex++).also {
                finalRound.add(it)
                allRounds.add(it)
            }
        }
        finalRound.leaves.forEach { round ->
            round.teams = Pair(
                teams.removeAt(Random.nextInt(0, teams.size)),
                if (teams.size >= 1) {
                    round.game = getGame()
                    teams.removeAt(Random.nextInt(0, teams.size))
                } else null
            )
        }
        advanceRound()
    }

    private fun isOnFinalRound() = finalRound == currentRound

    fun onRoundEnd(winningTeam: Team, winningPlayers: List<Player>? = null) {
        if (winningTeam == currentRound?.teams?.first) {
            currentRound!!.teams.second?.let { losers.add(it) }
        } else {
            currentRound!!.teams.first?.let { losers.add(it) }
        }
        winningTeam.players.forEach {
            it.hasWon = false
        }
        winningPlayers?.forEach {
            it.hasWon = true
        }
        currentRound!!.winner = winningTeam
        currentRound!!.parent?.let { parent ->
            parent.teams = if (parent.teams.first == null) {
                Pair(winningTeam, parent.teams.second)
            } else {
                parent.game = getGame()
                Pair(parent.teams.first, winningTeam)
            }
        }
        if (!isOnFinalRound()) advanceRound()
    }

    private fun getGame(): Game {
        if (games.isEmpty()) {
            games.addAll(selectedGames)
            selectedGames.clear()
        }
        return games.removeAt(Random.nextInt(0, games.size))
            .also {
                selectedGames.add(it)
                it.onSelected()
            }
    }


    private fun advanceRound() {
        currentRound = allRounds.findLast { it.isSwitchable || it.isProtoRound }
        if (currentRound!!.isProtoRound) {
            onRoundEnd(currentRound!!.teams.first ?: currentRound!!.teams.second!!)
        }
    }

    override fun toString(): String {
        return finalRound.traversePreOrder(currentRound = currentRound!!)
    }

    fun makeLosersBracket(): Bracket {
        return Bracket(games, losers, selectedGames)
    }

    fun rerollGame() {
        val oldGame = currentRound!!.game!!
        currentRound!!.game = getGame()
        selectedGames.remove(oldGame)
        games.add(oldGame)
    }

    fun changeRound(roundIndex: Int): String {
        if (roundIndex > allRounds.size) {
            return "there are only ${allRounds.size} rounds"
        }
        val round = allRounds[roundIndex - 1]
        if (!round.isSwitchable) return "Round is unreachable (either undecided matchup or already won)"
        currentRound = round
        return "switched to round $roundIndex"
    }
}

