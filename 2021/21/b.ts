import _ from 'lodash'

const input = `
Player 1 starting position: 4
Player 2 starting position: 8
`

const player1Position = +input.match(/Player 1 starting position: (\d+)/)![1]
const player2Position = +input.match(/Player 2 starting position: (\d+)/)![1]

const DICE_SUM_PERMUTATIONS = [
  [3, 1], // 111
  [4, 3], // 211, 121, 112
  [5, 6], // 311, 131, 113, 221, 212, 122
  [6, 7], // 321, 312, 231, 213, 132, 123, 222
  [7, 6], // 331, 313, 133, 322, 232, 223
  [8, 3], // 332, 323, 233
  [9, 1] // 333
]

function move(position: number, distance: number): number {
  return ((position + distance - 1) % 10) + 1
}

function play(
  player1Position: number,
  player2Position: number,
  player1Turn: boolean,
  player1Score = 0,
  player2Score = 0
): number {
  if (player1Score >= 21) return 1
  if (player2Score >= 21) return 0

  return _.sumBy(DICE_SUM_PERMUTATIONS, ([sum, permutations]) => {
    const landedOn = move(player1Turn ? player1Position : player2Position, sum)
    const wins = player1Turn
      ? play(landedOn, player2Position, false, player1Score + landedOn, player2Score)
      : play(player1Position, landedOn, true, player1Score, player2Score + landedOn)

    return permutations * wins
  })
}

console.log(
  _.max([
    play(player1Position, player2Position, true),
    play(player2Position, player1Position, false)
  ])
)
