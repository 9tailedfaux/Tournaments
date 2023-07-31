import kotlin.random.Random

class Game(private val name: String, val maxPlayersPerTeam: Int, val subGames: ArrayList<Game>? = null) {
    var chosenSubgame: Game? = null
    fun onSelected() {
        chosenSubgame = subGames?.removeAt(Random.nextInt(0, subGames.size))
    }

    override fun toString(): String {
        return if (chosenSubgame == null) "$name (${maxPlayersPerTeam}v${maxPlayersPerTeam})"
            else chosenSubgame.toString()
    }

}
