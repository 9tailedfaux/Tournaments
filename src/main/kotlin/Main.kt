fun main() {
    val games = arrayListOf(
        Game("rocket league", 1),
        Game("smash bros ultimate", 2),
        Game("mario kart wii", 1),
        Game("smash bros brawl", 1),
        Game("doom 1993", 1),
        Game("guilty gear strive", 1),
        Game("dragon ball fighter z", 1),
        Game("skullgirls 2nd encore", 1),
        Game("mario kart 8", 2),
        Game("tetris effect: connected", 1),
        Game("crash team racing nitro fueled", 2),
        Game("mighty switch force", 1),
        Game(
            "wii sports", 1, arrayListOf(
                SubGame("tennis", 1),
                SubGame("baseball", 1),
                SubGame("bowling", 1),
                SubGame("golf (3 holes)", 1),
            )
        ),
        Game(
            "wii sports resort", 1, arrayListOf(
                SubGame("swordplay duel", 1),
                SubGame("swordplay speed slice", 1),
                SubGame("wakeboarding", 1),
                SubGame("basketball pickup contest", 1),
                SubGame("bowling", 1),
                SubGame("canoeing", 1),
                SubGame("golf (3 holes)", 1),
                SubGame("table tennis", 1),
                SubGame("airsports dogfight", 1),
            )
        ),
        Game("tekken 7", 1),
        Game("dr mario", 1),
        Game("dr robotnik's mean bean machine", 1),
        Game("ultimate chicken horse", 2),
        Game("kirby air ride", 1),
        Game("overcooked 2", 1),
        Game("touhou genso rondo", 1),
        Game("atari flashbacks", 1),
        Game("dragon ball fighterz", 1),
        Game("soulcalibur vi", 1),
        Game("melty blood: type lumina", 1)

    )
    val teams = arrayListOf(
        Team(
            "team 1", arrayListOf(
                Player("me"),
                Player("you"),
                Player("physically sick"),
                Player("mentally thick"),
                Player("fu"),
                Player("ifgaf")
            )
        ),
        Team(
            "team 2", arrayListOf(
                Player("jason"),
                Player("derulo"),
                Player("derulo2")
            )
        ),
        Team(
            "team 3", arrayListOf(
                Player("happy"),
                Player("girls"),
                Player("ASJSIEGSRG"),
                Player("skfhsghsog")
            )
        ),
        Team(
            "team 4", arrayListOf(
                Player("lucky"),
                Player("GIRLS")
            )
        ),
        Team(
            "team 5", arrayListOf(
                Player("PRE"),
                Player("CURE"),
                Player("HAPPY"),
                Player("LOVE")
            )
        ),
        /*Team("team 6", arrayListOf(
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
        ))*/
    )

    var exit = false
    var input: String

    var bracket = Bracket(games, teams).apply { generate() }
    var firstWinner: Team? = null
    var secondWinner: Team? = null
    println(bracket)

    while (!exit) {
        print("tournament> ")
        input = readln()
        when (input) {
            "exit" -> {
                println("Are you sure you want to exit? (y/n)")
                readln().let {
                    when (it) {
                        "y", "Y" -> {
                            println("exiting...")
                            exit = true
                        }
                    }
                }
            }

            "print" -> println(bracket)
            "end" -> endRound(bracket)
            "reroll" -> reroll(bracket)
            "help" -> printHelp()
        }

        if (bracket.finalRound.winner != null) {
            if (firstWinner == null) {
                firstWinner = bracket.finalRound.winner
                println("do you want to do a loser's bracket now? (y/n)")
                readln().lowercase().replace(" ", "").elementAt(0).let {
                    if (it == 'y') {
                        bracket = bracket.makeLosersBracket().apply { generate() }
                        println(bracket)
                    } else {
                        println("Game over! ${bracket.currentRound!!.winner} wins!")
                        exit = true
                    }
                }
            } else if (secondWinner == null) {
                secondWinner = bracket.finalRound.winner!!
                bracket = Bracket(
                    bracket.games,
                    arrayListOf(firstWinner, secondWinner),
                    bracket.selectedGames
                ).apply { generate() }
                println(bracket)
            } else {
                println("Game over! ${bracket.currentRound!!.winner} wins!")
                exit = true
            }
        }
    }
}

fun printHelp() {
    println("exit:    terminates the program")
    println("print:   prints the current bracket in its current state")
    println("end:     ends the current round and asks you some questions about who won before advancing to the next round")
    println("reroll:  draw a different game for the current round, recycling the existing game back into the pool")
    println("help:    displays this message")
}

fun reroll(bracket: Bracket) {
    bracket.rerollGame()
    println(bracket)
}

fun endRound(bracket: Bracket) {
    println("which team won?")
    println("1: ${bracket.currentRound!!.teams.first}")
    println("2: ${bracket.currentRound!!.teams.second}")

    val teamInput = readln().let {
        when (it) {
            "1" -> bracket.currentRound!!.teams.first!!
            else -> bracket.currentRound!!.teams.second!!
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
                .replace(" ", "")
                .ifBlank { null }
                ?.split(",")
                ?.map { teamInput.players[it.toInt() - 1] }

            if (playersInput == null) {
                continue
            }
            else if (playersInput.size > bracket.currentRound!!.game!!.maxPlayersPerTeam) {
                println("Entered too many players. ${bracket.currentRound!!.game!!.name} only supports ${bracket.currentRound!!.game!!.maxPlayersPerTeam} player${if (bracket.currentRound!!.game!!.maxPlayersPerTeam == 1) "" else "s" } per team")
                continue
            }

            cont = true
        } catch (e: Exception) {
            println("Caught exception \"$e\". Please try again")
        }
    } while (!cont)

    bracket.onRoundEnd(teamInput, playersInput)

    println(bracket)
}