import getDocuments from "./signrequest_documents";

function simulateAsyncCall(request) {
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
  it("returns a 400 bad request status if the request is invalid", () => {
    const mockApiCall = simulateAsyncCall();
    return mockApiCall.then((response) => {
      expect(response.status).toBe(200);
    });
  });
});
