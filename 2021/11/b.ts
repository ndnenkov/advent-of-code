import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
5483143223
2745854711
5264556173
6141336146
6357385478
4167524645
2176841721
6882881134
4846848554
5283751526
`

const cave = readInput(sampleInput)
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

function findSynchronizationStep(cave: number[][]): number {
  const octopusCount = _.flatten(cave).length

  for (let stepNumber = 1; ; stepNumber++) {
    const [nextCave, flashes] = step(cave)
    if (flashes === octopusCount) return stepNumber
    cave = nextCave
  }
}

console.log(findSynchronizationStep(cave))
