import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
Player 1 starting position: 4
Player 2 starting position: 8
`

const input = readInput(sampleInput)

const player1Position = +input.match(/Player 1 starting position: (\d+)/)![1]
const player2Position = +input.match(/Player 2 starting position: (\d+)/)![1]

function row3Times(seed: number): [number, number] {
  return [3 * (seed + 1), seed + 3]
}

function move(position: number, die: number): [number, number] {
  const [distance, dieAfterRow] = row3Times(die)
  const landedOn = ((position + distance - 1) % 10) + 1

  return [landedOn, dieAfterRow]
}

function play(
  player1Position: number,
  player2Position: number,
  die = 1,
  player1Score = 0,
  player2Score = 0,
  player1Turn = true
): number {
  if (player1Score >= 1000) return (die - 1) * player2Score
  if (player2Score >= 1000) return (die - 1) * player1Score

  const [landedOn, dieAfterRow] = move(player1Turn ? player1Position : player2Position, die)

  return player1Turn
    ? play(landedOn, player2Position, dieAfterRow, player1Score + landedOn, player2Score, false)
    : play(player1Position, landedOn, dieAfterRow, player1Score, player2Score + landedOn, true)
}

console.log(play(player1Position, player2Position))
