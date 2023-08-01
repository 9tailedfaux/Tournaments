import kotlin.random.Random

class Bracket(private val games: ArrayList<Game>, private val teams: ArrayList<Team>) {
    private val allRounds = ArrayList<Round>()
    private val selectedGames = ArrayList<Game>()
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

    private fun getGame(): Game =
        games.removeAt(Random.nextInt(0, games.size))
            .also {
                selectedGames.add(it)
                it.onSelected()
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
}

