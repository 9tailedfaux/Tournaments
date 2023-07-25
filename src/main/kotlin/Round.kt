open class Round() {
    var parent: Round? = null
    var game: Game? = null
    var left: Round? = null
    var right: Round? = null
    var teams: Pair<Team?, Team?> = Pair(null, null)
    var winner: Team? = null
    val isLeaf: Boolean
        get() = left == null && right == null

    val isUnfilled: Boolean
        get() = (left != null) xor (right != null)

    override fun toString(): String {
        return if (winner == null) {
            "${teams.first ?: "?"} vs ${teams.second ?: "?"}: " +
                    if (teams.first != null && teams.second != null)
                        "$game bans: ${
                            teams.first?.getBans(game!!.maxPlayersPerTeam)
                        } ${
                            teams.second?.getBans(
                                game!!.maxPlayersPerTeam
                            )
                        }" else "?"
        } else {
            "${game ?: "Round"} won by $winner!"
        }
    }


    companion object {
        fun add(root: RootRound, new: Round) {
            val parent: Round = if (root.unfilled.isNotEmpty()) {
                root.unfilled.removeLast()
            } else if (root.leaves.isNotEmpty()) {
                root.leaves.removeFirst()
            } else {
                root
            }

            new.parent = parent

            if (parent.left == null) {
                parent.left = new
            } else {
                parent.right = new
            }

            parent.apply {
                if (!isUnfilled) root.unfilled.remove(this)
                else if (!root.unfilled.contains(this)) root.unfilled.add(this)
            }

            root.leaves.add(new)
        }

        fun traverseRounds(
            sb: StringBuilder,
            padding: String,
            pointer: String,
            round: Round?,
            hasRightSibling: Boolean,
            currentRound: Round
        ) {
            val borderChar = if (currentRound == round) "=" else "-"
            if (round != null) {
                sb.append("\n$padding│  ")
                for (i in 0 until round.toString().length) {
                    sb.append(borderChar)
                }
                sb.append("\n")
                sb.append(padding)
                sb.append(pointer)
                sb.append(round.toString())
                sb.append("\n$padding   ")
                for (i in 0 until round.toString().length) {
                    sb.append(borderChar)
                }

                val paddingBuilder = StringBuilder(padding).apply {
                    append(
                        if (hasRightSibling) {
                            "│  "
                        } else {
                            "   "
                        }
                    )
                }

                val paddingForBoth = paddingBuilder.toString()
                val pointerRight = "└──"
                val pointerLeft = if (round.right != null) "├──" else "└──"

                traverseRounds(sb, paddingForBoth, pointerLeft, round.left, round.right != null, currentRound)
                traverseRounds(sb, paddingForBoth, pointerRight, round.right, false, currentRound)
            }
        }
    }
}

class RootRound(game: Game): Round() {
    init {
        this.game = game
    }
    val unfilled = ArrayList<Round>()
    val leaves = ArrayList<Round>()
    fun getLeafCount() = leaves.size
    fun add(new: Round) {
        add(this, new)
    }

    fun traversePreOrder(root: RootRound = this, currentRound: Round): String {
        val sb: StringBuilder = StringBuilder()
        val borderChar = if (currentRound == root) "=" else "-"
        for (i in 0 until root.toString().length) {
            sb.append(borderChar)
        }
        sb.append("\n$root\n")
        for (i in 0 until root.toString().length) {
            sb.append(borderChar)
        }

        val pointerForRight = "└──"
        val pointerForLeft = if (root.right != null) "├──" else "└──"

        traverseRounds(sb, "", pointerForLeft, root.left, root.right != null, currentRound)
        traverseRounds(sb, "", pointerForRight, root.right, false, currentRound)

        return sb.toString()
    }
}