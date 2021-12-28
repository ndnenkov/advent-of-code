import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010
`

const diagnostics = readInput(sampleInput)
  .trim()
  .split('\n')
  .map(number => number.split('').map(bit => +bit as 0 | 1))

function rating(numbers: (0 | 1)[][], bias: 0 | 1, index = 0): number {
  if (numbers.length === 1) return parseInt(numbers[0].join(''), 2)

  const bitCriteria = _.sum(_.unzip(numbers)[index]) * 2 >= numbers.length ? bias : 1 - bias

  return rating(
    numbers.filter(number => number[index] === bitCriteria),
    bias,
    index + 1
  )
}

console.log(rating(diagnostics, 1) * rating(diagnostics, 0))
