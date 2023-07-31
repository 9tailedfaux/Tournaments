import kotlin.random.Random

open class Bracket(private val games: ArrayList<Game>, private val teams: ArrayList<Team>): Resettable {
    protected val allRounds = ArrayList<Round>()
    private val selectedGames = ArrayList<Game>()
    protected val roundQueue = ArrayList<Round>()
    protected val finalRound: RootRound = RootRound(
        getGame(),
        "main bracket"
    ).also { allRounds.add(it) }
    var currentRound: Round? = null

    fun generate() {
        generate(finalRound, allRounds, teams, roundQueue)
    }

    protected fun generate(finalRound: RootRound, allRounds: ArrayList<Round>, teams: ArrayList<Team>, roundQueue: ArrayList<Round>) {
        if (teams.isEmpty()) return
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
        roundQueue.addAll(allRounds)
        advanceRound()
    }

    open val hasWinner
        get() = winner != null
    open val winner
        get() = finalRound.winner

    private fun isOnFinalRound() = finalRound == currentRound

    open fun onRoundEnd(winningTeam: Team, winningPlayers: List<Player>? = null) {
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

    protected fun getGame(): Game {
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

    protected open fun getNextRound(): Round? = roundQueue.removeLast()

    protected open fun advanceRound() {
        getNextRound()?.let {
            currentRound = it
            handleBypass()
        }
    }

    private fun handleBypass() {
        if ((currentRound!!.teams.first == null) xor (currentRound!!.teams.second == null)) {
            onRoundEnd(currentRound!!.teams.first ?: currentRound!!.teams.second!!)
        }
    }

    override fun toString(): String {
        return finalRound.traversePreOrder(currentRound = currentRound!!)
    }

    override fun reset() {
        currentRound = allRounds.last()
        roundQueue.apply {
            clear()
            addAll(allRounds)
        }
        allRounds.forEach {
            it.reset()
        }
        handleBypass()
    }
}

