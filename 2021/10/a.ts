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

function errorScore(character: Closing): number {
  switch (character) {
    case ')':
      return 3
    case ']':
      return 57
    case '}':
      return 1197
    case '>':
      return 25137
  }
}

function findSyntaxError(line: Character[], parsed: Opening[] = [], index = 0): number {
  if (index === line.length) return 0

  const character = line[index]

  if (isOpening(character)) return findSyntaxError(line, parsed.concat(character), index + 1)
  if (_.last(parsed) === opposite(character))
    return findSyntaxError(line, _.dropRight(parsed), index + 1)

  return errorScore(character)
}

console.log(_.sum(code.map(line => findSyntaxError(line))))
