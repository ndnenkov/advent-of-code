import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
1163751742
1381373672
2136511328
3694931569
7463417111
1319128137
1359912421
3125421639
1293138521
2311944581
`

const cave = readInput(sampleInput)
  .trim()
  .split('\n')
  .map(line => line.split('').map(risk => +risk))

function neighbors(cave: number[][], x: number, y: number): [number, number][] {
  return [
    [0, -1],
    [0, 1],
    [1, 0],
    [-1, 0]
  ]
    .map(([dx, dy]) => [x + dx, y + dy] as [number, number])
    .filter(([nx, ny]) => cave[ny]?.[nx] !== undefined)
}

function dijkstra(
  cave: number[][],
  startX: number,
  startY: number,
  endX: number,
  endY: number
): number {
  const queue = [[startX, startY]]
  const visited = new Set<string>()
  visited.add(`${startX},${startY}`)

  const distances = cave.map(row => row.map(() => Infinity))
  distances[startY][startX] = 0

  while (queue.length) {
    const minDistance = _.min(_.flatMap(queue.map(([x, y]) => distances[y][x])))!
    const nextIndexInQueue = queue.findIndex(([x, y]) => distances[y][x] === minDistance)
    const [x, y] = queue.splice(nextIndexInQueue, 1)[0]

    neighbors(cave, x, y).forEach(([nx, ny]) => {
      distances[ny][nx] = _.min([distances[ny][nx], distances[y][x] + cave[ny][nx]])!

      if (!visited.has(`${nx},${ny}`)) {
        queue.push([nx, ny])
        visited.add(`${nx},${ny}`)
      }
    })
  }

  return distances[endY][endX]
}

function increment(cave: number[][]): number[][] {
  return cave.map(row => row.map(risk => (risk === 9 ? 1 : risk + 1)))
}

function concatSideways(firstMatrix: number[][], secondMatrix: number[][]): number[][] {
  return firstMatrix.map((row, i) => [...row, ...secondMatrix[i]])
}

function concatDownwards(firstMatrix: number[][], secondMatrix: number[][]): number[][] {
  return [...firstMatrix, ...secondMatrix]
}

function revealCave(cave: number[][]) {
  const plus1 = increment(cave)
  const plus2 = increment(plus1)
  const plus3 = increment(plus2)
  const plus4 = increment(plus3)
  const plus5 = increment(plus4)
  const plus6 = increment(plus5)
  const plus7 = increment(plus6)
  const plus8 = increment(plus7)

  return [
    [cave, plus1, plus2, plus3, plus4],
    [plus1, plus2, plus3, plus4, plus5],
    [plus2, plus3, plus4, plus5, plus6],
    [plus3, plus4, plus5, plus6, plus7],
    [plus4, plus5, plus6, plus7, plus8]
  ]
    .map(row => row.reduce(concatSideways))
    .reduce(concatDownwards)
}

const wholeCave = revealCave(cave)

console.log(dijkstra(wholeCave, 0, 0, wholeCave[0].length - 1, wholeCave.length - 1))
