import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
3,4,3,1,2
`

const fishes = readInput(sampleInput)
  .trim()
  .split(',')
  .map(age => +age as 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8)

const population = _.memoize((age: number): number =>
  age > 0 ? population(age - 9) + population(age - 7) : 1
)

console.log(_.sum(fishes.map(age => population(256 - age))))
