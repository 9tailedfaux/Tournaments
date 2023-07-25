import java.lang.NumberFormatException

fun main(args: Array<String>) {
    val games = arrayListOf(
        Game("rocket league", 1),
        Game("smash bros ultimate", 2),
        Game("mario kart wii", 2),
        Game("smash bros brawl", 1),
        Game("doom 1993", 1),
        Game("guilty gear strive", 1),
        Game("dragon ball fighter z", 1),
        Game("skullgirls 2nd encore", 1),
        Game("mario kart 8", 2),
        Game("tetris", 1),
        Game("crash team racing nitro fueled", 2),
        Game("mighty switch force", 1),
        Game("wii sports", 1),
        Game("wii sports resort", 1),
        Game("tekken 7", 1),
        Game("dr mario", 1),
        Game("dr robotnik's mean bean machine", 1),
        Game("ultimate chicken horse", 2),
        Game("kirby air ride", 1)

    )
    val teams = arrayListOf(
        Team("team 1", arrayListOf(
            Player("me"),
            Player("you")
        )),
        Team("team 2", arrayListOf(
            Player("jason"),
            Player("derulo")
        )),
        Team("team 3", arrayListOf(
            Player("happy"),
            Player("girls")
        )),
        Team("team 4", arrayListOf(
            Player("lucky"),
            Player("GIRLS")
        )),
        Team("team 5", arrayListOf(
            Player("PRE"),
            Player("CURE")
        )),
        Team("team 6", arrayListOf(
            Player("HAPPY"),
            Player("LOVE")
        )),
        Team("team 7", arrayListOf(
            Player("ASJSIEGSRG"),
            Player("skfhsghsog")
        )),
        Team("team 8", arrayListOf(
            Player("json"),
            Player("derulo")
        )),
        Team("team 9", arrayListOf(
            Player("microsoft"),
            Player("office")
        )),
        Team("team 10", arrayListOf(
            Player("subtle"),
            Player("tea")
        )),
        Team("team 11", arrayListOf(
            Player("connection"),
            Player("established")
        )),
        Team("team 12", arrayListOf(
            Player("physically sick"),
            Player("mentally thick")
        )),
        Team("team 13", arrayListOf(
            Player("haha"),
            Player("jonathan")
        )),
        Team("team 14", arrayListOf(
            Player("obamna..."),
            Player("SODA!!!!!")
        )),
        Team("team 15", arrayListOf(
            Player("michaelsoft"),
            Player("binbows")
        )),
        Team("team 16", arrayListOf(
            Player("fu"),
            Player("ifgaf")
        )),
        Team("team 17", arrayListOf(
            Player("let the"),
            Player("destruction begin")
        ))
    )

    var exit = false
    var input: String

    val bracket = Bracket(games, teams).apply { generate() }
    println(bracket)

    while (!exit) {
        print("tournament> ")
        input = readln()
        when (input) {
            "exit" -> {
                println("exiting...")
                exit = true
            }
            "print" -> println(bracket)
            "end" -> endRound(bracket)
        }

        if (bracket.finalRound.winner != null) {
            println("Game over! ${bracket.currentRound!!.winner} wins!")
            exit = true
        }
    }
}

fun endRound(bracket: Bracket) {
    println("which team won?")
    println("1: ${bracket.currentRound!!.teams.first}")
    println("2: ${bracket.currentRound!!.teams.second}")

    val teamInput = readln().let {
        if (it == "1") {
            bracket.currentRound!!.teams.first!!
        } else {
            bracket.currentRound!!.teams.second!!
        }
    }

    var playersInput: List<Player>? = null
    var cont = false
    do {
        try {
            println("which players won? (separate by commas)")
            for ((index, player) in teamInput.players.withIndex()) {
                println("${index + 1}: $player")
            }

            playersInput = readln()
                .ifBlank { null }
                ?.replace(" ", "")
                ?.split(",")
                ?.map { teamInput.players[it.toInt() - 1] }

            cont = true
        } catch (e: Exception) {
            println("Caught exception \"$e\". Please try again")
        }
    } while (!cont)

    bracket.onRoundEnd(teamInput, playersInput)

    println(bracket)
}