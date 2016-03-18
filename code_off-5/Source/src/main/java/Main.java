import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.model.Transition;
import es.usc.citius.hipster.model.function.CostFunction;
import es.usc.citius.hipster.model.function.impl.StateTransitionFunction;
import es.usc.citius.hipster.model.problem.ProblemBuilder;
import es.usc.citius.hipster.model.problem.SearchProblem;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by hennie.brink on 2016/03/18.
 */
public class Main {

    public static void main(String... args){

        Path path = Paths.get("W:\\Projects\\Personal\\CodeOff\\Source\\code_off-5\\code_off-5-2.in");
        try {

            new Main().solveProblem(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    char[][] maze;
    private void solveProblem(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
        maze = new char[lines.size()][lines.get(0).length()];

        Point origin = new Point(0, 0);
        Point goal = new Point(0,0);
        for (int y = 0; y < lines.size(); y++) {

            String line = lines.get(y);

            for (int x = 0; x < line.length(); x++) {

                char charAt = line.charAt(x);
                maze[x][y] = charAt;

                if(charAt == '@')
                    origin = new Point(x, y);
                if(charAt == 'U')
                    goal = new Point(x, y);
            }
        }

        SearchProblem p = ProblemBuilder.create()
                .initialState(origin)
                .defineProblemWithoutActions()
                .useTransitionFunction(new StateTransitionFunction<Point>() {
                    @Override
                    public Iterable<Point> successorsOf(Point state) {
                        // The transition function returns a collection of transitions.
                        // A transition is basically a class Transition with two attributes:
                        // source point (from) and destination point (to). Our source point
                        // is the current point argument. We have to compute which are the
                        // available movements (destination points) from the current point.
                        // Class Maze has a helper method that tell us the empty points
                        // (where we can move) available:
                        return validLocationsFrom(state);
                    }
                })
                .useCostFunction(new CostFunction<Void, Point, Double>() {
                    // We know now how to move (transitions) from each tile. We need to define the cost
                    // of each movement. A diagonal movement (for example, from (0,0) to (1,1)) is longer
                    // than a top/down/left/right movement. Although this is straightforward, if you don't
                    // know why, read this http://www.policyalmanac.org/games/aStarTutorial.htm.
                    // For this purpose, we define a CostFunction that computes the cost of each movement.
                    // The CostFunction is an interface with two generic types: S - the state, and T - the cost
                    // type. We use Points as states in this problem, and for example doubles to compute the distances:

                    public Double evaluate(Transition<Void, Point> transition) {
                        Point source = transition.getFromState();
                        Point destination = transition.getState();
                        // The distance from the source to de destination is the euclidean
                        // distance between these two points http://en.wikipedia.org/wiki/Euclidean_distance

                        boolean diagonal = source.getX() != destination.getY() && source.getY() != destination.getY();
                        return source.distance(destination);
                    }
                })
                .build();


        System.out.println("Start Location " + origin);
        System.out.println("Goal Location " + goal);

        Algorithm.SearchResult search = Hipster.createAStar(p).search(goal);
        List<List<Point>> optimalPaths = search.getOptimalPaths();

        for (int x = 0; x < maze.length; x++) {

            for (int y = 0; y < maze.length; y++) {

                char charAt = maze[x][y];

                if(charAt == ' ') {
                    for (List<Point> optimalPath : optimalPaths) {

                        for (Point point : optimalPath) {

                            if (point.y == y && point.x == x)
                                charAt = '.';
                        }
                    }
                }

                System.out.print(charAt);
            }
            System.out.println();
        }
    }

    public Collection<Point> validLocationsFrom(Point loc) {
        HashSet<Point> validMoves = new HashSet<Point>();

        for(int row = -1; row <= 1; ++row) {

            try {

                if(this.isFree(new Point(loc.x, loc.y + row))) {
                    validMoves.add(new Point(loc.x, loc.y + row));
                }
            } catch (ArrayIndexOutOfBoundsException var6) {
                System.out.println(var6);
            }
        }

        for(int column = -1; column <= 1; ++column) {
            try {

                if(this.isFree(new Point(loc.x + column, loc.y))) {
                    validMoves.add(new Point(loc.x + column, loc.y));
                }
            } catch (ArrayIndexOutOfBoundsException var6) {
                System.out.println(var6);
            }
        }

        validMoves.remove(loc);
        return validMoves;
    }

    private boolean isFree(Point point) {

        return maze[point.x][point.y] != '#';
    }
}
