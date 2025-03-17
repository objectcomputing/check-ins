# Templates

Place MJML templates here.  If they are going to be used within a call to `String.format()`, be sure to double up on any percent signs (i.e.,  `width="100%%"`).

### Micronaut Usage

```java
@Value("classpath:mjml/feedback_request.mjml")
private Readable feedbackRequestTemplate;
```
