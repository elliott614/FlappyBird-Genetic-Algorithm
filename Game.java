
public class Game {
	public int[] ht;
	private String pipes;
	
	public Game(String pipes) {
		this.pipes = pipes;
		this.ht = new int[pipes.length()];
		for (int i = 0; i < this.ht.length; i ++)
			this.ht[i] = pipes.charAt(i) - 48;
	}
	
	@Override
	public String toString() {
		return this.pipes;
	}

	
	
}
