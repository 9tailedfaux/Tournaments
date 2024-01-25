import kotlin.random.Random

open class Game(val name: String, val maxPlayersPerTeam: Int, private val subGames: ArrayList<SubGame>? = null) {
    private var chosenSubGame: SubGame? = null
    private val selectedSubGames = ArrayList<SubGame>()

    private fun getSubGame(): SubGame? {
        if (subGames == null) return null
        if (subGames.isEmpty()) {
            subGames.addAll(selectedSubGames)
            selectedSubGames.clear()
        }
        return subGames.removeAt(Random.nextInt(0, subGames.size))
            .also {
                selectedSubGames.add(it)
                it.onSelected()
            }
    }

    fun onSelected() {
        chosenSubGame = getSubGame()
    }
    override fun toString(): String {
        return if (chosenSubGame != null) "$name, $chosenSubGame"
        else "$name (${maxPlayersPerTeam}v${maxPlayersPerTeam})"
    }
}

class SubGame(name: String, maxPlayersPerTeam: Int) : Game(name, maxPlayersPerTeam, null) {

}