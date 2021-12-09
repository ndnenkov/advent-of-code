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

function neighbors(heightmap: number[][], x: number, y: number): number[] {
  return [
    heightmap[y][x - 1],
    heightmap[y][x + 1],
    heightmap[y - 1]?.[x],
    heightmap[y + 1]?.[x]
  ].filter(_.isNumber)
}

function lowPoints(heightmap: number[][]): number[] {
  const lowPoints: number[] = []

  heightmap.forEach((row, y) => {
    row.forEach((height, x) => {
      if (neighbors(heightmap, x, y).every(neighbor => neighbor > height)) lowPoints.push(height)
    })
  })

  return lowPoints
}

console.log(_.sum(lowPoints(heightmap).map(point => point + 1)))
