package se.magnus.api.composite.product;

public class ServiceAddresses {
  private final String composite;
  private final String product;
  private final String review;
  private final String recommendation;

  public ServiceAddresses() {
    composite = null;
    product = null;
    review = null;
    recommendation = null;
  }

  public ServiceAddresses(
    String compositeAddress,
    String productAddress,
    String reviewAddress,
    String recommendationAddress) {

    this.composite = compositeAddress;
    this.product = productAddress;
    this.review = reviewAddress;
    this.recommendation = recommendationAddress;
  }

  public String getComposite() {
    return composite;
  }

  public String getProduct() {
    return product;
  }

  public String getReview() {
    return review;
  }

  public String getRecommendation() {
    return recommendation;
  }
}
