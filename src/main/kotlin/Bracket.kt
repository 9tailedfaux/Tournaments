import kotlin.random.Random

class Bracket(val games: ArrayList<Game>, private val teams: ArrayList<Team>, val selectedGames: ArrayList<Game> = ArrayList()) {
    private val allRounds = ArrayList<Round>()
    private val losers = ArrayList<Team>()
    val finalRound: RootRound = RootRound(
        getGame()
    ).also { allRounds.add(it) }
    var currentRound: Round? = null

    fun generate() {
        while(finalRound.getLeafCount() < (teams.size / 2) + (teams.size % 2)) {
            Round().also {
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
        currentRound = allRounds.removeLast()
        if ((currentRound!!.teams.first == null) xor (currentRound!!.teams.second == null)) {
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
}

