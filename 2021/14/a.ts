import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
NNCB

CH -> B
HH -> N
CB -> H
NH -> C
HB -> C
HC -> B
HN -> C
NN -> C
BH -> H
NC -> B
NB -> B
BN -> B
BB -> N
BC -> B
CC -> N
CN -> C
`

const input = readInput(sampleInput)

const template = input.trim().split('\n\n')[0]

const rules = _.fromPairs(
  input
    .trim()
    .split('\n\n')[1]
    .split('\n')
    .map(line => line.split(' -> '))
)

function step(
  counts: {[key: string]: number},
  rules: {[key: string]: string}
): {[key: string]: number} {
  const updatedCounts: {[key: string]: number} = {}

  _.toPairs(counts).forEach(([pair, count]) => {
    const insertion = rules[pair]

    if (insertion) {
      const [before, after] = pair.split('')
      updatedCounts[`${before}${insertion}`] ??= 0
      updatedCounts[`${insertion}${after}`] ??= 0
      updatedCounts[`${before}${insertion}`] += count
      updatedCounts[`${insertion}${after}`] += count
    } else {
      updatedCounts[pair] ??= 0
      updatedCounts[pair] = count
    }
  })

  return updatedCounts
}

function initialCounts(polymer: string): {[key: string]: number} {
  const pairs = _.zip(_.dropRight(polymer.split('')), _.drop(polymer.split('')))
  const counts = _.countBy(pairs.map(([first, second]) => `${first}${second}`))
  counts[`${polymer.slice(-1)} `] = 1

  return counts
}

function run10Times(polymer: string, rules: {[key: string]: string}): {[key: string]: number} {
  let counts = initialCounts(polymer)

  for (let stepNumber = 1; stepNumber <= 10; stepNumber++) {
    counts = step(counts, rules)
  }

  return counts
}

function quantify(histogram: {[key: string]: number}): number {
  const counts: {[key: string]: number} = {}

  _.toPairs(histogram).forEach(([key, count]) => {
    const [first, _second] = key.split('')

    counts[first] ??= 0
    counts[first] += count
  })

  return _.max(_.values(counts))! - _.min(_.values(counts))!
}

console.log(quantify(run10Times(template, rules)))
