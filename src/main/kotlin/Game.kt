class Game(val name: String, val maxPlayersPerTeam: Int) {
    override fun toString(): String {
        return "$name (${maxPlayersPerTeam}v${maxPlayersPerTeam})"
    }
}
