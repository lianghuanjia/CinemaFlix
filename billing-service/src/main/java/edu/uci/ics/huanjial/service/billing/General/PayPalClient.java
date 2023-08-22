package edu.uci.ics.huanjial.service.billing.General;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.huanjial.service.billing.BillingService;
import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;
import org.glassfish.grizzly.servlet.HttpServletRequestImpl;

import javax.ws.rs.core.UriBuilder;


public class PayPalClient {
    public final static String clientID = "AYqIf-vjxLdhSlDGgfvMMozPFF1vKT8bchFWRAz34elR83zRw0C58VdAdLUMFnHu-ayM4Okyv8mWoQHg";
    public final static String clientSecret = "EB-G7r87uWRPlIpt5dnVMoCcqMJltMtTRGIoEMNVX8nUi0WRnFRlZdDC675zOVgEIC_GV6py7nJE9uU9";
    private String sum;

    public PayPalClient(String sum) {
        this.sum = sum;
    }

    public Map<String, Object> createPayment() throws PayPalRESTException{
        Map<String,Object> response = new HashMap<String, Object>();

        //set amount
        Amount amount = getAmount();

        //set transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactionS =  new ArrayList<Transaction>();
        transactionS.add(transaction);

        //set payer
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        //set payment
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactionS);

        //set Url
        RedirectUrls redirectUrlS = new RedirectUrls();
        String url = getUrl();
        String cancelUrl = url+"cancel";
        ServiceLogger.LOGGER.info("CancelURL: "+cancelUrl);
        redirectUrlS.setCancelUrl(cancelUrl);
        String returnUrl = url+"order/complete";
        redirectUrlS.setReturnUrl(returnUrl); //what is return url???
        ServiceLogger.LOGGER.info("ReturnURL: " + returnUrl);
        payment.setRedirectUrls(redirectUrlS);

        //set create payment
        Payment createdPayment;
        try{
            String redirectUrl = "";
            APIContext context = new APIContext(clientID, clientSecret, "sandbox");
            ServiceLogger.LOGGER.info("Got APIContext");
            createdPayment = payment.create(context);
            ServiceLogger.LOGGER.info("Created payment");
            if(createdPayment!=null)
                {
                List<Links> links = createdPayment.getLinks();
                for (Links link:links)
                    {
                    if(link.getRel().equals("approval_url"))
                        {
                        redirectUrl = link.getHref();
                        break;
                        }
                    }
                response.put("status", "success");
                response.put("redirect_url", redirectUrl);
                }
        }
        catch (PayPalRESTException e) {
            ServiceLogger.LOGGER.info("Error message: \n" + e);
            ServiceLogger.LOGGER.info("Error happened during payment creation!");
            throw new PayPalRESTException(e);
        }
        return response;
    }

    public static Map<String, Object> completePayment(String paymentId, String PayerID)throws PayPalRESTException{
        Map<String, Object> response = new HashMap();
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(PayerID);
        try {
            APIContext context = new APIContext(clientID, clientSecret, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);
            if(createdPayment!=null){
                response.put("status", "success");
                response.put("payment", createdPayment);
            }
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
            throw new PayPalRESTException(e);
        }
        return response;
    }

    private String getUrl(){
            String scheme = BillingService.getConfigs().getScheme();
            String hostName = BillingService.getConfigs().getHostName();
            Integer port = BillingService.getConfigs().getPort();
            String path = BillingService.getConfigs().getPath();
            ServiceLogger.LOGGER.info("In getUrl: url is: " + scheme+hostName+":"+port.toString()+path+"/");
            return scheme+hostName+":"+port.toString()+path+"/";
    }

    private Amount getAmount(){
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(this.sum);
        return amount;
    }
}
