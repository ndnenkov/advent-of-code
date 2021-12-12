import _ from 'lodash'

const input = `
[({(<(())[]>[[{[]{<()<>>
[(()[<>])]({[<{<<[]>>(
{([(<{}[<>[]}>{[]{[(<()>
(((({<>}<{<{<>}{[]{[]{}
[[<[([]))<([[{}[[()]]]
[{[{({}]{}}([{[{{{}}([]
{<[[]]>}<{[{[{[]{()[[[]
[<(<(<(<{}))><([]([]()
<{([([[(<>()){}]>(<<{{
<{([{{}}[<[[[<>{}]]]>[]]
`

type Opening = '(' | '[' | '{' | '<'
type Closing = ')' | ']' | '}' | '>'
type Character = Opening | Closing

const code = input
  .trim()
  .split('\n')
  .map(line => line.split('') as Character[])

function isOpening(character: Character): character is Opening {
  return ['(', '[', '{', '<'].includes(character)
}

function opposite(character: Closing): Opening {
  switch (character) {
    case ')':
      return '('
    case ']':
      return '['
    case '}':
      return '{'
    case '>':
      return '<'
  }
}

function characterCompletionScore(character: Opening): number {
  return ['(', '[', '{', '<'].indexOf(character) + 1
}

function completionScore(unclosed: Opening[]): number {
  let score = 0

  unclosed
    .map(characterCompletionScore)
    .reverse()
    .forEach(characterScore => (score = score * 5 + characterScore))

  return score
}

function findCompletionScore(line: Character[], parsed: Opening[] = [], index = 0): number {
  if (index === line.length) return completionScore(parsed)

  const character = line[index]

  if (isOpening(character)) return findCompletionScore(line, parsed.concat(character), index + 1)
  if (_.last(parsed) === opposite(character))
    return findCompletionScore(line, _.dropRight(parsed), index + 1)

  return 0
}

const completionScores = _(code)
  .map(line => findCompletionScore(line))
  .compact()
  .sortBy()
  .value()

console.log(completionScores[Math.floor(completionScores.length / 2)])
