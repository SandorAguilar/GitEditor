/**
 * Edit distance
 * Deletion has cost 1, substitution has cost 2 (follows Levenshtein)
 * 
 * @author Eitan
 *
 */
public class EditDistance {

	
//	public static void main(String[] args){
//		String s1 = "eitan";
//		String s2 =  "erica";
//		System.out.println("distance(" + s1 + ", " + s2 + ") = " + EditDistance.calcDistance(s1, s2));
//		s1 = "intention";
//		s2 = "execution";
//		System.out.println("distance(" + s1 + ", " + s2 + ") = " + EditDistance.calcDistance(s1, s2));
//	}
//	
	
	/**
	 * Calculates edit distance between s1 and s2
	 * @param s1
	 * @param s2
	 * @return distance
	 */
	public static int calcDistance(String s1, String s2){
		int r = s1.length();
		int c = s2.length();
		Integer[][] mat = new Integer[r + 1][c + 1];
//		Initialize mat(s1, 0)		
		for (int i = 0; i < r + 1; i++){
			mat[i][0] = i;
		}
//		Initialize mat(0, s2)
		for (int j = 0; j < c + 1; j++){
			mat[0][j] = j;
		}
		for (int i = 1; i < r + 1; i++){
			for (int j = 1; j < c + 1; j++){
				int delS1 = mat[i - 1][j] + 1;
				int delS2 = mat[i][j - 1] + 1;
				int replace = mat[i - 1][j - 1];
				if (s1.charAt(i - 1) != s2.charAt(j - 1)) replace += 2;
				mat[i][j] = Math.min(delS2, Math.min(delS1, replace));
			}
		}
		return mat[r][c];
		
	}
	
	
}
