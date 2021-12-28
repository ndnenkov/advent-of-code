import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
16,1,2,0,4,2,7,1,2,14
`

const crabs = readInput(sampleInput)
  .trim()
  .split(',')
  .map(position => +position)

function fuelCost(crabs: number[], position: number): number {
  return _.sumBy(crabs, crab => Math.abs(crab - position))
}

function median(numbers: number[]): number {
  const sorted = _.sortBy(numbers)

  if (sorted.length % 2 === 0) {
    const above = sorted[sorted.length / 2 - 1]
    const below = sorted[sorted.length / 2]
    return Math.floor((above + below) / 2)
  } else {
    return sorted[sorted.length / 2 - 1]
  }
}

console.log(fuelCost(crabs, median(crabs)))
