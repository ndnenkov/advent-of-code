import _ from 'lodash'

const input = `
4134384626
7114585257
1582536488
4865715538
5733423513
8532144181
1288614583
2248711141
6415871681
7881531438
`

const cave = input
  .trim()
  .split('\n')
  .map(octopuses => octopuses.split('').map(octopus => parseInt(octopus)))

function neighbors(cave: number[][], x: number, y: number): number[][] {
  return [
    [x, y - 1],
    [x, y + 1],
    [x - 1, y],
    [x + 1, y],
    [x - 1, y - 1],
    [x - 1, y + 1],
    [x + 1, y - 1],
    [x + 1, y + 1]
  ].filter(([x, y]) => cave[y]?.[x] !== undefined)
}

function energize(cave: number[][]): number[][] {
  return cave.map(row => row.map(octopus => octopus + 1))
}

function flashout(cave: number[][]): number[][] {
  const nextCave = _.cloneDeep(cave)
  const flashedOctopuses = new Set<string>()

  let flashed = false
  do {
    flashed = false
    nextCave.forEach((row, y) => {
      row.forEach((octopus, x) => {
        if (octopus > 9 && !flashedOctopuses.has(`${x},${y}`)) {
          flashed = true
          flashedOctopuses.add(`${x},${y}`)

          neighbors(nextCave, x, y).forEach(([nx, ny]) => {
            nextCave[ny][nx] += 1
          })
        }
      })
    })
  } while (flashed)

  return nextCave
}

function flashCount(cave: number[][]): number {
  return _.flatten(cave).filter(octopus => octopus > 9).length
}

function calmDown(cave: number[][]): number[][] {
  return cave.map(row => row.map(octopus => (octopus > 9 ? 0 : octopus)))
}

function step(cave: number[][]): [number[][], number] {
  const nextCave = flashout(energize(cave))
  const flashes = flashCount(nextCave)

  return [calmDown(nextCave), flashes]
}

function run100Times(cave: number[][]): number {
  let flashes = 0

  for (let i = 0; i < 100; i++) {
    const [nextCave, nextFlashes] = step(cave)
    cave = nextCave
    flashes += nextFlashes
  }

  return flashes
}

console.log(run100Times(cave))
