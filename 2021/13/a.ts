import _ from 'lodash'

const input = `
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

const matrix = input
  .trim()
  .split('\n\n')[0]
  .split('\n')
  .map(row => row.split(',').map(coordinate => +coordinate) as [number, number])
const instructions = input
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

console.log(fold(matrix, instructions[0][0], instructions[0][1]).length)
