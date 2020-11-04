package bookmarket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class PaymentController {

 @Autowired
 PaymentRepository paymentRepository;


 @GetMapping("/circuitBreaker")
 @HystrixCommand(fallbackMethod = "fallback", commandProperties = {
         @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"), //5초만에 fallback 호출
         @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000"),
         @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10"),
         @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "1"),//n번 개까지 정상 동작
         @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")//10초동안 circuitBreaker 동작
 })
 public String circuitBreakerTest(@RequestParam String isYn) throws InterruptedException {

  if (isYn.equals("Y")) {
   System.out.println("@@@ CircuitBreaker!!!");
   Thread.sleep(10000);
   //throw new RuntimeException("CircuitBreaker!!!");
  }

  System.out.println("$$$ SUCCESS!!!");
  return " SUCCESS!!!";
 }

 @GetMapping("/selectDeliveryInfo")
 @HystrixCommand(fallbackMethod = "fallbackDelivery", commandProperties = {
         @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
         @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000"),
         @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10"),
         @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "1"),
         @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
 })
 public String selectPaymentInfo(@RequestParam long orderId) throws InterruptedException {

  if (orderId <= 0) {
   System.out.println("@@@ CircuitBreaker!!!");
   Thread.sleep(10000);
   //throw new RuntimeException("CircuitBreaker!!!");
  } else {
   Optional<Payment> payment = paymentRepository.findById(orderId);
   return payment.get().getPaymentStatus();
  }

  System.out.println("$$$ SUCCESS!!!");
  return " SUCCESS!!!";
 }

 private String fallback(String isYn) {
  System.out.println("### fallback!!!");
  return "CircuitBreaker!!!";
 }

 private String fallbackDelivery(long orderId) {
  System.out.println("### fallback!!!");
  return "CircuitBreaker!!!";
 }
}
