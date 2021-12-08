import _ from 'lodash'

const input = `
16,1,2,0,4,2,7,1,2,14
`

const crabs = input
  .trim()
  .split(',')
  .map(position => +position)

function fuelCost(crabs: number[], position: number): number {
  return _.sumBy(crabs, crab => {
    const distance = Math.abs(crab - position)
    return (distance * (distance + 1)) / 2
  })
}

const belowAverageCost = fuelCost(crabs, Math.floor(_.mean(crabs)))
const aboveAverageCost = fuelCost(crabs, Math.ceil(_.mean(crabs)))
console.log(_.min([belowAverageCost, aboveAverageCost]))
