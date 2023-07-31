class BracketWithLosers(games: ArrayList<Game>, teams: ArrayList<Team>) : Bracket(games, teams) {
    private val losers = ArrayList<Team>()
    private val actualFinalRound = RootRound(getGame(), "final showdown")
    private val allLosersRounds = ArrayList<Round>()
    private val losersRoundsQueue = ArrayList<Round>()
    private val losersFinalRound = RootRound(getGame(), "loser's bracket").also {
        allLosersRounds.add(it)
    }

    private var currentBracket: RootRound = finalRound
    private var currentRoundQueue: ArrayList<Round>? = roundQueue

    override val hasWinner
        get() = actualFinalRound.winner != null
    override val winner
        get() = actualFinalRound.winner

    fun onRoundEnd(winningTeam: Team, winningPlayers: List<Player>? = null, losingTeam: Team?) {
        super.onRoundEnd(winningTeam, winningPlayers)
        losingTeam?.let { losers.add(it) }
    }

    override fun advanceRound() {
        if (currentRoundQueue?.lastOrNull() == finalRound) {
            generate(losersFinalRound, allLosersRounds, losers, losersRoundsQueue)
            currentBracket = losersFinalRound
            currentRoundQueue = losersRoundsQueue
        } else if (currentRoundQueue?.lastOrNull() == losersFinalRound) {
            currentBracket = actualFinalRound
            currentRoundQueue = null
            currentRound = currentBracket
        }
        super.advanceRound()
    }

    override fun getNextRound(): Round? {
        return currentRoundQueue?.removeLastOrNull()
    }

    override fun toString(): String {
        return "${currentBracket.name}\n${currentBracket.traversePreOrder(currentRound = currentRound!!)}"
    }

    override fun reset() {
        allLosersRounds.forEach {
            it.reset()
        }
        allLosersRounds.clear()
        losersRoundsQueue.clear()
        losers.clear()
        super.reset()
    }
}