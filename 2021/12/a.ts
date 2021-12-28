import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
start-A
start-b
A-c
A-b
b-d
A-end
b-end
`

const graph = readInput(sampleInput)
  .trim()
  .split('\n')
  .map(line => line.split('-'))
  .reduce((graph, [from, to]) => {
    graph[from] = graph[from] || []
    graph[to] = graph[to] || []
    graph[from].push(to)
    graph[to].push(from)
    return graph
  }, {} as {[key: string]: string[]})

function bfs(graph: {[key: string]: string[]}, start: string, end: string): string[][] {
  const queue: string[][] = [[start]]
  const completePaths: string[][] = []

  while (queue.length) {
    const path = queue.shift()!
    const node = path[path.length - 1]
    if (node === end) completePaths.push(path)

    graph[node]?.forEach(next => {
      if (next !== next.toLowerCase() || !path.includes(next)) queue.push([...path, next])
    })
  }

  return completePaths
}

console.log(bfs(graph, 'start', 'end').length)
