import _ from 'lodash'

const input = `
forward 5
down 5
forward 8
up 3
down 8
forward 2
`

const instructions = input
  .trim()
  .split('\n')
  .map(instruction => instruction.split(' '))

let position = 0
let depth = 0
instructions.forEach(([instruction, amount]) => {
  if (instruction === 'forward') position += +amount
  if (instruction === 'down') depth += +amount
  if (instruction === 'up') depth -= +amount
})

console.log(position * depth)
