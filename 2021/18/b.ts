import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
[[[5,[2,8]],4],[5,[[9,9],0]]]
[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
[[[[5,4],[7,7]],8],[[8,3],8]]
[[9,3],[[9,9],[6,[4,9]]]]
[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
`

const homework = readInput(sampleInput).trim().split('\n')

interface Number {
  value: number
  nesting: number
}

function parseSnailfishNumber(snailfishNumber: string): Number[] {
  const numbers: Number[] = []
  let nesting = 0

  snailfishNumber.split('').forEach(character => {
    if (character === '[') nesting++
    else if (character === ']') nesting--
    else if (character !== ',') numbers.push({value: parseInt(character), nesting})
  })

  return numbers
}

function split(numbers: Number[]): Number[] {
  const indexToSplit = numbers.findIndex(number => number.value > 9)

  if (indexToSplit < 0) return numbers

  const number = numbers[indexToSplit]

  const first = {value: Math.floor(number.value / 2), nesting: number.nesting + 1}
  const second = {value: number.value - first.value, nesting: number.nesting + 1}

  const splitNumbers = _.cloneDeep(numbers)
  splitNumbers.splice(indexToSplit, 1, first, second)

  return splitNumbers
}

function explode(numbers: Number[]): Number[] {
  const explodeFirstIndex = numbers.findIndex(number => number.nesting > 4)

  if (explodeFirstIndex < 0) return numbers

  const explodeSecondIndex = explodeFirstIndex + 1
  const first = numbers[explodeFirstIndex]
  const second = numbers[explodeSecondIndex]

  const explodedNumbers = _.cloneDeep(numbers)

  if (explodeFirstIndex) explodedNumbers[explodeFirstIndex - 1].value += first.value

  explodedNumbers.splice(explodeFirstIndex, 2, {value: 0, nesting: first.nesting - 1})

  if (explodeSecondIndex < explodedNumbers.length)
    explodedNumbers[explodeSecondIndex].value += second.value

  return explodedNumbers
}

function nestDeeper(number: Number): Number {
  return {value: number.value, nesting: number.nesting + 1}
}

function addSnailfishNumbers(first: Number[], second: Number[]): Number[] {
  let sum = first.map(nestDeeper).concat(second.map(nestDeeper))

  while (true) {
    const explodedSum = explode(sum)
    if (sum !== explodedSum) {
      sum = explodedSum
      continue
    }

    const splitSum = split(sum)
    if (sum !== splitSum) {
      sum = splitSum
      continue
    }

    return sum
  }
}

function magnitude(numbers: Number[]): number {
  if (numbers.length === 1) return numbers[0].value

  const [deepestNumber, deepestIndex] = _.maxBy(
    numbers.map((number, index) => [number, index] as [Number, number]),
    ([number, _index]) => number.nesting
  )!

  const value = 3 * deepestNumber.value + 2 * numbers[deepestIndex + 1].value

  numbers.splice(deepestIndex, 2, {value: value, nesting: deepestNumber.nesting - 1})

  return magnitude(numbers)
}

function findMaxSumMagnitude(snailfishNumbers: string[]): number {
  const numbers = snailfishNumbers.map(parseSnailfishNumber)

  let maxMagnitude = 0

  numbers.forEach(first => {
    numbers.forEach(second => {
      if (first !== second) {
        const sum = addSnailfishNumbers(first, second)
        const sumMagnitude = magnitude(sum)
        if (sumMagnitude > maxMagnitude) maxMagnitude = sumMagnitude
      }
    })
  })

  return maxMagnitude
}

console.log(findMaxSumMagnitude(homework))
