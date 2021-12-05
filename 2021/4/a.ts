import _ from 'lodash'

const input = `
7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7
`

const numbers = input
  .split('\n')[1]
  .split(',')
  .map(number => parseInt(number))

const boards = _.drop(input.split('\n\n')).map(board =>
  board
    .trim()
    .split('\n')
    .map(row =>
      row
        .trim()
        .split(/\s+/)
        .map(number => +number)
    )
)

function winning(board: number[][]): boolean {
  return _.some([board, _.unzip(board)], boardVariation =>
    _.some(boardVariation, row => _.every(row, cell => cell === -1))
  )
}

let winningBoard: undefined | number[][] = undefined
const winningNumber = numbers.find(number => {
  boards.forEach(board => {
    board.forEach(row => {
      row.forEach((cell, index) => {
        if (cell === number) row[index] = -1
      })
    })
  })

  winningBoard = boards.find(board => winning(board))

  return !!winningBoard
})!

const boardSum = _.sum(_.flatten(winningBoard).filter(number => number !== -1))
console.log(boardSum * winningNumber)
