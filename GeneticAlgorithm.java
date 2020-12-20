import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GeneticAlgorithm {

	public static final Game GAME1 = new Game(
			"0000000000000000000000000000000000400050000700000000055065000000600605000067076006000000600005405005");
	public static final Game GAME2 = new Game(
			"0000000000000000000040000000030000000030000010000001000000000000100000000200021020000050000000600070");
	public static final Game GAME3 = new Game(
			"0000060000000000000000000000002000000000000000000010000000200000012030200000010000000000020002000030");
	public static final Game GAME4 = new Game(
			"0000000200000030002000000000000000070000000400000600700000000000500060000050000000000707670006000050");
	public static final Game GAME5 = new Game(
			"0000000000000000000000000000000000000000660000000707000000000500000020000000000010220045050020300211");
	public static final int AVERAGE_SCORE_THRESHOLD = 70;
	public static final String OUTPUT_PATH = "./output.txt";
	public static final String POPULATION_PATH = "./population.txt";
	public static final int MAX_GENERATIONS = 100;

	public static void main(String[] args) {

		System.out.println("--------=====******GAME 1********=======---------");
		Bird[] birds1 = geneticAlgorithm(GAME1);
		System.out.println("--------=====******GAME 2********=======---------");
		Bird[] birds2 = geneticAlgorithm(GAME2);
		System.out.println("--------=====******GAME 3********=======---------");
		Bird[] birds3 = geneticAlgorithm(GAME3);
		System.out.println("--------=====******GAME 4********=======---------");
		Bird[] birds4 = geneticAlgorithm(GAME4);
		System.out.println("--------=====******GAME 5********=======---------");
		Bird[] birds5 = geneticAlgorithm(GAME5);
		System.out.println("done");

		// write output.txt
		try {
			BufferedWriter bwOutput = new BufferedWriter(new FileWriter(OUTPUT_PATH));

			for (int i = 0; i < birds1.length - 1; i++)
				bwOutput.write("" + birds1[i].score + ",");
			bwOutput.write("" + birds1[birds1.length - 1].score);
			bwOutput.newLine();

			for (int i = 0; i < birds2.length - 1; i++)
				bwOutput.write("" + birds2[i].score + ",");
			bwOutput.write("" + birds2[birds2.length - 1].score);
			bwOutput.newLine();

			for (int i = 0; i < birds3.length - 1; i++)
				bwOutput.write("" + birds3[i].score + ",");
			bwOutput.write("" + birds3[birds3.length - 1].score);
			bwOutput.newLine();

			for (int i = 0; i < birds4.length - 1; i++)
				bwOutput.write("" + birds4[i].score + ",");
			bwOutput.write("" + birds4[birds4.length - 1].score);
			bwOutput.newLine();

			for (int i = 0; i < birds5.length - 1; i++)
				bwOutput.write("" + birds5[i].score + ",");
			bwOutput.write("" + birds5[birds5.length - 1].score);
			bwOutput.close();
			
			//write population.txt
			BufferedWriter bwPopulation = new BufferedWriter(new FileWriter(POPULATION_PATH));
			bwPopulation.write(GAME2.toString());
			bwPopulation.newLine();
			for (int i = 0; i < birds2.length; i++) {
				bwPopulation.write(birds2[i].toString());
				bwPopulation.newLine();
			}
			bwPopulation.close();
			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Bird[] geneticAlgorithm(Game game) {
		int previousTotalScore = -1;
		Bird[] birds = randomBirds(100, 100);
		playGame(birds, game);
		int totalScore = totalScore(birds);
		System.out.println("First generation average score: " + (totalScore + 0.) / 100.);

		Bird[] previousBirds = birds;

		// successive generations while total score improves
		for (int i = 2; totalScore > previousTotalScore || totalScore < 100 * AVERAGE_SCORE_THRESHOLD && i < MAX_GENERATIONS; i++) {
			previousBirds = birds;
			previousTotalScore = totalScore;
			birds = reproduce(birds);
			playGame(birds, game);
			totalScore = totalScore(birds);
			System.out.println("Generation " + i + " 's average score: " + (totalScore + 0.) / 100.);
		}
		return totalScore > previousTotalScore ? birds : previousBirds; // return generation with highest score, NOT
																		// final generation
	}

	// create random birds for first generation
	public static Bird[] randomBirds(int numberOfBirds, int numberOfMoves) {
		Bird[] birds = new Bird[numberOfBirds];

		Random rng = new Random();

		for (int i = 0; i < numberOfBirds; i++) {

			String moves = "";

			for (int j = 0; j < numberOfMoves; j++)
				if (rng.nextDouble() < 1. / 3.)
					moves += "1";
				else
					moves += "0";

			birds[i] = new Bird(moves);

		}

		return birds;
	}

	// create new generation based on scores.
	public static Bird[] reproduce(Bird[] parents) {
		Bird[] children = new Bird[parents.length];

		// calculate reproduction probabilities
		int totalScore = totalScore(parents);
		for (int i = 0; i < parents.length; i++)
			parents[i].reproductionProbability = (parents[i].score + 0.) / (0. + totalScore);

		for (int j = 0; j < 50; j++) {
			// choose parents using CDF
			double cumulative = 0.0;
			Random rng = new Random();
			double r1 = rng.nextDouble();
			double r2 = rng.nextDouble();
			Bird mother = new Bird("");
			Bird father = new Bird("");
			for (int i = 0; i < parents.length; i++) {
				cumulative += parents[i].reproductionProbability;
				if (cumulative >= r1 && mother.score == 0)
					mother = parents[i];
				if (cumulative >= r2 && father.score == 0)
					father = parents[i];
			}
			// crossover mother and father, mutate their 2 children
			Bird[] siblings = crossover(mother, father);
			Bird[] mf = { mother, father };
			mutate(siblings[0], bestBird(mf).score);
			mutate(siblings[1], bestBird(mf).score);

			// add to children
			children[j] = siblings[0];
			children[j + 50] = siblings[1];

		}

		return children;
	}
	
	// Changed crossover to simple 1-point crossover
	public static Bird[] crossover(Bird bird1, Bird bird2) {
		Bird[] children = new Bird[2];

		Bird winner = bird1.score > bird2.score ? bird1 : bird2;
		Bird loser = bird1.score > bird2.score ? bird2 : bird1;

		children[0] = new Bird(
				winner.toString().substring(0, loser.score - 1) + loser.toString().substring(loser.score - 1));
		children[1] = new Bird(
				loser.toString().substring(0, loser.score - 1) + winner.toString().substring(loser.score - 1));

		return children;
	}
	
	// bird with higher score crosses over at midpoint of dying positions of both
	// birds
	// bird with lower score takes higher scoring bird's moves some positions
	// before lower scoring bird's death
	public static Bird[] crossover2(Bird bird1, Bird bird2) {
		Bird[] children = new Bird[2];

		Bird winner = bird1.score > bird2.score ? bird1 : bird2;
		Bird loser = bird1.score > bird2.score ? bird2 : bird1;

		children[0] = new Bird(winner.toString().substring(0, (winner.score + loser.score) / 2)
				+ loser.toString().substring((winner.score + loser.score) / 2));
		children[1] = new Bird(
				loser.toString().substring(0, loser.score - 2) + winner.toString().substring(loser.score - 2));

		return children;
	}

	// mutate a bird (replaces bird by a new bird with mutated moves)
	public static void mutate(Bird bird) {
		Random rng = new Random();
		int[] moves = new int[bird.moves.length];
		for (int i = 0; i < bird.moves.length; i++) {
			if (rng.nextDouble() < 0.01)
				moves[i] = 1 - bird.moves[i];
		}
		bird = new Bird(moves);
	}
	
	// mutate a bird (replaces bird by a new bird with mutated moves) Mutates more after threshold in score parameter
	public static void mutate(Bird bird, int score) {
		Random rng = new Random();
		int[] moves = new int[bird.moves.length];
		for (int i = 0; i < bird.moves.length; i++) {
			if (rng.nextDouble() < 0.02)
				moves[i] = 1 - bird.moves[i];
			if (rng.nextDouble() < 0.6 && i >= score) // mutate more if after death spot
				moves[i] = 1 - bird.moves[i];
		}
		bird = new Bird(moves);
	}

	// update scores for all birds
	public static void playGame(Bird[] birds, Game game) {
		for (int i = 0; i < 100; i++)
			birds[i].play(game);
	}

	public static int totalScore(Bird[] birds) {
		int total = 0;
		for (int i = 0; i < birds.length; i++)
			total += birds[i].score;
		return total;
	}

	public static Bird bestBird(Bird[] birds) {
		int bestScore = 0;
		Bird bestBird = null;
		for (int i = 0; i < birds.length; i++)
			if (birds[i].score > bestScore) {
				bestBird = birds[i];
				bestScore = birds[i].score;
			}
		return bestBird;
	}
}
