/* *****************************************************************************
 *  Name: BaseballElimination.java
 *  Date: 3/2/2020
 *  Description: decide whether a team is methmatically eliminated
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class BaseballElimination {
    private final int numberOfTeams;
    private final String[] teams;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] against;
    private int maxWin;
    private int maxId;
    private final HashMap<String, Integer> teamId = new HashMap<>();
    private final HashMap<String, Bag<String>> certificateOfElimination = new HashMap<>();

    // create a baseball division from given filename
    public BaseballElimination(String filename) {
        if (filename == null)
            throw new IllegalArgumentException("The file cannot be null");
        In in = new In(filename);
        numberOfTeams = in.readInt();
        teams = new String[numberOfTeams];
        wins = new int[numberOfTeams];
        losses = new int[numberOfTeams];
        remaining = new int[numberOfTeams];
        against = new int[numberOfTeams][numberOfTeams];
        maxWin = 0;
        maxId = 0;
        for (int i = 0; i < numberOfTeams; ++i) {
            String team = in.readString();
            teams[i] = team;
            teamId.put(team, i);
            int win = in.readInt();
            if (win > maxWin) {
                maxWin = win;
                maxId = i;
            }
            wins[i] = win;
            losses[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < numberOfTeams; ++j)
                against[i][j] = in.readInt();
        }
        for (int i = 0; i < numberOfTeams; ++i)
            checkEliminated(i);
    }

    // number of teams
    public int numberOfTeams() {
        return numberOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return teamId.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        if (!teamId.containsKey(team))
            throw new IllegalArgumentException("Invalid team name");
        return wins[teamId.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!teamId.containsKey(team))
            throw new IllegalArgumentException("Invalid team name");
        return losses[teamId.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!teamId.containsKey(team))
            throw new IllegalArgumentException("Invalid team name");
        return remaining[teamId.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!teamId.containsKey(team1) || !teamId.containsKey(team2))
            throw new IllegalArgumentException("Invalid team name");
        return against[teamId.get(team1)][teamId.get(team2)];
    }

    private void checkEliminated(int id) {
        if (wins[id] + remaining[id] < maxWin) {
            Bag<String> cert = new Bag<>();
            cert.add(teams[maxId]);
            certificateOfElimination.put(teams[id], cert);
            return;
        }
        int gamesVert = (numberOfTeams - 1) * (numberOfTeams - 2) / 2;
        int teamsVert = numberOfTeams - 1;
        int v = 2 + gamesVert + teamsVert;
        FlowNetwork fn = new FlowNetwork(v);
        int curVert = 1;
        int totalGames = 0;
        for (int i = 0; i < numberOfTeams; ++i) {
            if (i == id) continue;
            for (int j = i + 1; j < numberOfTeams; ++j) {
                if (j == id) continue;
                int ordi = id < i ? i - 1 : i;
                int ordj = id < j ? j - 1 : j;
                fn.addEdge(new FlowEdge(0, curVert, against[i][j]));
                fn.addEdge(new FlowEdge(curVert, 1 + gamesVert + ordi, Double.POSITIVE_INFINITY));
                fn.addEdge(new FlowEdge(curVert, 1 + gamesVert + ordj, Double.POSITIVE_INFINITY));
                ++curVert;
                totalGames += against[i][j];
            }
        }
        for (int i = 0; i < teamsVert; ++i) {
            int vert = i >= id ? i + 1 : i;
            fn.addEdge(
                    new FlowEdge(1 + gamesVert + i, v - 1, wins[id] + remaining[id] - wins[vert]));
        }

        FordFulkerson ff = new FordFulkerson(fn, 0, v - 1);
        if (totalGames - ff.value() > 0.5) {
            Bag<String> cert = new Bag<>();
            for (int i = 0; i < numberOfTeams - 1; ++i) {
                if (ff.inCut(i + 1 + gamesVert)) {
                    if (i < id)
                        cert.add(teams[i]);
                    else
                        cert.add(teams[i + 1]);
                }
            }
            certificateOfElimination.put(teams[id], cert);
        }
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!teamId.containsKey(team))
            throw new IllegalArgumentException("Invalid team name");
        return certificateOfElimination.containsKey(team);
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!isEliminated(team))
            return null;
        return certificateOfElimination.get(team);
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
