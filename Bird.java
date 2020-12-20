
public class Bird {
	public int[] moves;
	public int score;
	private String string;
	private final double X = .5; //how much bird rises when flaps
	private final double Y = .25; //how much bird descends due to gravity when doesn't flap
	public double reproductionProbability;

	public Bird(String string) {
		this.reproductionProbability = 0.0;
		this.string = string;
		this.score = 0; //signals that bird hasn't played
		this.moves = new int[string.length()];
		for (int i = 0; i < this.moves.length; i++)
			this.moves[i] = string.charAt(i) - 48;
	}
	
	public Bird(int[] moves) {
		this.reproductionProbability = 0.0;
		this.moves = moves;
		this.score = 0; //signals that bird hasn't played
		String string = "";
		for (int i = 0; i < this.moves.length; i++)
			string += moves[i];
		this.string = string;
	}
	
	//sets score of bird by playing game passed to method
	public void play(Game game) {
		int score = 0;
		double position = 5;
		
		for (int t = 0; t < game.ht.length; t++) {
			
			//check if hit a pipe
			if (game.ht[t] != 0 && (position < game.ht[t] || position > game.ht[t] + 2))
				break;
			
			//check if flew off screen
			else if (position < 0 || position > 10)
				break;
			
			//increment score
			score++;

			//update position based on moves
			if (moves[t] == 1)
				position += X;
			else position -= Y;
			
		}
		
		this.score = score;
	}

	@Override
	public String toString() {
		return this.string;
	}
	
}
