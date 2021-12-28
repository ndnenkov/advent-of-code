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

function rate(diagnostics: (0 | 1)[][], bias: 0 | 1): number {
  const rate = _.unzip(diagnostics)
    .map(bits => (_.sum(bits) * 2 > bits.length ? bias : 1 - bias))
    .join('')

  return parseInt(rate, 2)
}

console.log(rate(diagnostics, 1) * rate(diagnostics, 0))
