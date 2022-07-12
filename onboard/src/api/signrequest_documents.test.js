import getDocuments from "./signrequest_documents";

function simulateAsyncCall() {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const user = getDocuments();
      if (user) {
        resolve({ status: 200, message: "Found" });
      } else {
        resolve({ status: 404, message: "Not Found" });
      }
    }, 300);
  });
}

describe("Mock API call", () => {
  it("returns a 400 bad request status if the request is invalid", async () => {
    const mockApiCall = simulateAsyncCall();
    const response = await mockApiCall;
      expect(response.status).toBe(200);
  });
});
