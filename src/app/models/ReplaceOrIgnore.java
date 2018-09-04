package app.models;

	public enum ReplaceOrIgnore {
		 
		Ignore("I", "Ignore"), Replace("R", "Replace");
		 
		   private String code;
		   private String text;
		 
		   private ReplaceOrIgnore(String code, String text) {
		       this.code = code;
		       this.text = text;
		   }
		 
		   public String getCode() {
		       return code;
		   }
		 
		   public String getText() {
		       return text;
		   }
		 
		   public static ReplaceOrIgnore getByCode(String genderCode) {
		       for (ReplaceOrIgnore g : ReplaceOrIgnore.values()) {
		           if (g.code.equals(genderCode)) {
		               return g;
		           }
		       }
		       return null;
		   }
		 
		   @Override
		   public String toString() {
		       return this.text;
		   }
		 
	}
