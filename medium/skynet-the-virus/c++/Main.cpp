#include <iostream>
#include <string>
#include <vector>
#include <queue>
#include <algorithm>
#include <stdexcept>

using namespace std;

struct Node {
	int id;
	bool isGateway;
	vector<int> connections;
};

struct Path
{
	Node* currentNode;
	Path* previousPath;

	Path(Node* const currentNode, Path* const previousPath) : currentNode(currentNode), previousPath(nullptr)
	{
		if (previousPath)
		{
			this->previousPath = new Path(previousPath->currentNode, previousPath->previousPath);
		}
	}

	Path(const Path& that) : currentNode(that.currentNode), previousPath(nullptr)
	{
		if (that.previousPath)
		{
			this->previousPath = new Path(that.previousPath->currentNode, that.previousPath->previousPath);
		}
	}

	~Path()
	{
		if (this->previousPath)
		{
			delete this->previousPath;
		}
	}

	Path& operator=(const Path& that)
	{
		if (this != &that)
		{

			Path* newPreviousPath = (that.previousPath) ? new Path(that.previousPath->currentNode, that.previousPath->previousPath) : nullptr;

			delete this->previousPath;

			this->currentNode = that.currentNode;
			this->previousPath = newPreviousPath;

		}
		return *this;
	}

};

struct Link {
	Node* node1;
	Node* node2;
};

Path findShortestPathToGateway(Node& startNode, vector<Node>& graph) {

	Path currentPath{ &startNode, nullptr};
	queue<Path> queue;
	queue.push(currentPath);


	while (!queue.empty()) {
		currentPath = queue.front();
		queue.pop();
		if (currentPath.currentNode->isGateway) {
			return currentPath;
		} else {
			for (int i : currentPath.currentNode->connections) {
				queue.push(Path{ &graph[i], &currentPath });
			}
		}
	}

	throw runtime_error("Skynet agent is not able to reach any gateway, we win!");
}

Link findLinkToSevere(Node& skynetAgent, vector<Node>& graph) {
	Path shortestPathToGateway = findShortestPathToGateway(skynetAgent, graph);
	return Link{ shortestPathToGateway.currentNode, shortestPathToGateway.previousPath->currentNode };
}

void severeLink(const Link& linkToSevere) {
	cout << linkToSevere.node1->id << " " << linkToSevere.node2->id << endl;
}

void updateNet(const Link& link, vector<Node>& graph) {
	vector<int>& node1Connections = graph[link.node1->id].connections;
	vector<int>& node2Connections = graph[link.node2->id].connections;
	node1Connections.erase(remove(node1Connections.begin(), node1Connections.end(), link.node2->id), node1Connections.end());
	node2Connections.erase(remove(node2Connections.begin(), node2Connections.end(), link.node1->id), node2Connections.end());
}

int main()
{
	int N; // the total number of nodes in the level, including the gateways
	int L; // the number of links
	int E; // the number of exit gateways
	cin >> N >> L >> E; cin.ignore();

	vector<Node> graph(N, Node());
	for (int i = 0; i < N; i++) {
		graph[i].id = i;
	}

	for (int i = 0; i < L; i++) {
		int N1, N2; // N1 and N2 define a link between these nodes
		cin >> N1 >> N2; cin.ignore();
		graph[N1].connections.push_back(N2);
		graph[N2].connections.push_back(N1);
	}

	for (int i = 0; i < E; i++) {
		int EI; // the index of a gateway node
		cin >> EI; cin.ignore();
		graph[EI].isGateway = true;
	}

	// game loop
	while (1) {

		int SI; // The index of the node on which the Skynet agent is positioned this turn
		cin >> SI; cin.ignore();

		Link link = findLinkToSevere(graph[SI], graph);
		severeLink(link);
		updateNet(link, graph);

	}

}