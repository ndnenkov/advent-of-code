import _ from 'lodash'

const input = `
2199943210
3987894921
9856789892
8767896789
9899965678
`

const heightmap = input
  .trim()
  .split('\n')
  .map(line => line.split('').map(height => +height))

function neighbors(heightmap: number[][], x: number, y: number): number[][] {
  return [
    [x - 1, y],
    [x + 1, y],
    [x, y - 1],
    [x, y + 1]
  ].filter(([x, y]) => heightmap[y]?.[x] !== undefined)
}

function lowPoints(heightmap: number[][]): [number, number][] {
  const lowPoints: [number, number][] = []

  heightmap.forEach((row, y) => {
    row.forEach((height, x) => {
      if (neighbors(heightmap, x, y).every(([nx, ny]) => heightmap[ny][nx] > height))
        lowPoints.push([x, y])
    })
  })

  return lowPoints
}

function basin(heightmap: number[][], x: number, y: number): number {
  if (heightmap[y][x] === 9) return 0

  heightmap[y][x] = 9

  return 1 + _.sum(neighbors(heightmap, x, y).map(([nx, ny]) => basin(heightmap, nx, ny)))
}

const basins = _.sortBy(lowPoints(heightmap).map(([x, y]) => basin(heightmap, x, y)))
console.log(_.takeRight(basins, 3).reduce((a, b) => a * b))
