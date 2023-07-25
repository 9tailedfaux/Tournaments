class Team(private val name: String, val players: ArrayList<Player>) {

    /*val size: Int
        get() = players.size*/

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
}