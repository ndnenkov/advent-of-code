import _ from 'lodash'

const input = `
be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
`

const notes = input
  .trim()
  .split('\n')
  .map(line => line.split(' | ').map(digits => digits.split(' ')))

function chars(string: string): string[] {
  return string.split('')
}

function numericalDigit(mapping: string[], digit: string): number {
  return mapping.findIndex(mappedDigit =>
    _.isEqual(_.sortBy(chars(mappedDigit)), _.sortBy(chars(digit)))
  )
}

function deduceMapping(digits: string[]): string[] {
  const mapping: string[] = []

  mapping[1] = digits.find(digit => digit.length === 2)!
  mapping[4] = digits.find(digit => digit.length === 4)!
  mapping[7] = digits.find(digit => digit.length === 3)!
  mapping[8] = digits.find(digit => digit.length === 7)!

  mapping[9] = _.difference(digits, mapping).find(
    digit => digit.length === 6 && !_.difference(chars(mapping[4]), chars(digit)).length
  )!

  mapping[6] = _.difference(digits, mapping).find(
    digit => digit.length === 6 && _.difference(chars(mapping[1]), chars(digit)).length
  )!

  mapping[0] = _.difference(digits, mapping).find(digit => digit.length === 6)!

  mapping[3] = _.difference(digits, mapping).find(
    digit => !_.difference(chars(mapping[1]), chars(digit)).length
  )!

  mapping[5] = _.difference(digits, mapping).find(
    digit => !_.difference(chars(digit), chars(mapping[6])).length
  )!

  return mapping
}

const sum = _.sumBy(notes, ([signals, number]) => {
  const mapping = deduceMapping(signals)
  return parseInt(number.map(digit => numericalDigit(mapping, digit)).join(''))
})

console.log(sum)
