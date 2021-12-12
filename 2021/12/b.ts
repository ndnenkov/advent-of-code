import _ from 'lodash'

const input = `
start-A
start-b
A-c
A-b
b-d
A-end
b-end
`

const graph = input
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

    const visitedSmallNodeTwice = path
      .filter(node => node === node.toLowerCase())
      .some(node => path.indexOf(node) !== path.lastIndexOf(node))

    graph[node]?.forEach(next => {
      const canRevisitNode =
        (next !== 'start' && next !== 'end' && !visitedSmallNodeTwice) ||
        next !== next.toLowerCase()

      if (canRevisitNode || !path.includes(next)) queue.push([...path, next])
    })
  }

  return completePaths
}

console.log(bfs(graph, 'start', 'end').length)
