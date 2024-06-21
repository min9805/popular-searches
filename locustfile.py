from locust import HttpUser, task, between, TaskSet

class UserBehavior(TaskSet):

    @task(10)
    def add_keyword_weight10(self):
        self.client.post("/api/v1/searchRank/search", params={"keyword": "WEIGHT_10"})

    @task(7)
    def add_keyword_weight7(self):
        self.client.post("/api/v1/searchRank/search", params={"keyword": "WEIGHT_7"})

    @task(5)
    def add_keyword_weight5(self):
        self.client.post("/api/v1/searchRank/search", params={"keyword": "WEIGHT_5"})

    @task(3)
    def add_keyword_weight3(self):
        self.client.post("/api/v1/searchRank/search", params={"keyword": "WEIGHT_3"})

    @task(1)
    def add_keyword_weight1(self):
        self.client.post("/api/v1/searchRank/search", params={"keyword": "WEIGHT_1"})

    @task(1)
    def get_top_keywords(self):
        self.client.get("/api/v1/searchRank/rank", params={""})

class WebsiteUser(HttpUser):
    tasks = [UserBehavior]
    wait_time = between(1, 5)
    host = "http://localhost:8080"  # Replace with your Spring Boot application's host