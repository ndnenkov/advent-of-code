import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
6,10
0,14
9,10
0,3
10,4
4,11
6,0
6,12
4,1
0,13
10,12
3,4
3,0
8,4
1,10
2,14
8,10
9,0

fold along y=7
fold along x=5
`

const matrix = readInput(sampleInput)
  .trim()
  .split('\n\n')[0]
  .split('\n')
  .map(row => row.split(',').map(coordinate => +coordinate) as [number, number])
const instructions = readInput(sampleInput)
  .trim()
  .split('\n\n')[1]
  .split('\n')
  .map(row => row.replace('fold along ', '').split('=') as ['x' | 'y', number])

function fold(matrix: [number, number][], side: 'x' | 'y', at: number): [number, number][] {
  const convertCoordinate = (coordinate: number): number =>
    coordinate > at ? 2 * at - coordinate : coordinate

  const convert = ([x, y]: [number, number]): [number, number] => [
    side === 'x' ? convertCoordinate(x) : x,
    side === 'y' ? convertCoordinate(y) : y
  ]

  return _.uniqBy(matrix.map(convert), ([x, y]) => `${x},${y}`)
}

function foldAll(
  matrix: [number, number][],
  instructions: ['x' | 'y', number][]
): [number, number][] {
  instructions.forEach(([side, at]) => {
    matrix = fold(matrix, side, at)
  })

  return matrix
}

function printMatrix(matrix: [number, number][]) {
  const maxX = _.max(matrix.map(([x, _y]) => x))!
  const maxY = _.max(matrix.map(([_x, y]) => y))!

  for (let y = 0; y <= maxY; y++) {
    for (let x = 0; x <= maxX; x++) {
      const inMatrix = matrix.some(([mx, my]) => mx === x && my === y)
      process.stdout.write(inMatrix ? '░' : ' ')
    }
    process.stdout.write('\n')
  }
}

printMatrix(foldAll(matrix, instructions))
