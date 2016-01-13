package eu.europeana.annotation.definitions.model.moderation;


public interface Summary {

	public int getTotal();

	public int getEndorseSum();

	public void setEndorseSum(int endorseSum);

	public int getReportSum();

	public void setReportSum(int reportSum);

	public boolean equalsContent(Object other);

}
