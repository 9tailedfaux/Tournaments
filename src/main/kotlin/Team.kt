class Team(private val name: String, val players: ArrayList<Player>): Resettable {

    fun getBans(numPlayersNeeded: Int): List<Player>? {
        val bans = players.filter { it.hasWon }
        return if (players.size - bans.size < numPlayersNeeded) {
            null
        } else {
            bans
        }
    }

    override fun toString(): String {
        return name
    }

    override fun reset() {
        players.forEach {
            it.hasWon = false
        }
    }
}