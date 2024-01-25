import kotlin.random.Random

open class Game(val name: String, val maxPlayersPerTeam: Int, private val subGames: ArrayList<SubGame>? = null) {
    private var chosenSubGame: SubGame? = null

    fun onSelected() {
        chosenSubGame = subGames?.removeAt(Random.nextInt(0, subGames.size))
    }
    override fun toString(): String {
        return if (chosenSubGame != null) "$name, $chosenSubGame"
        else "$name (${maxPlayersPerTeam}v${maxPlayersPerTeam})"
    }
}

class SubGame(name: String, maxPlayersPerTeam: Int) : Game(name, maxPlayersPerTeam, null) {

}