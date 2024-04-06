
import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.time.LocalDate;

import org.junit.Test;

public class MonthlyChargeTests {
	static final int subscriptionId = 1;
	static final int customerId = 1;
	static final int monthlyPriceInCents = 5000;

	Subscription plan = new Subscription(subscriptionId, customerId, monthlyPriceInCents);

	@Test
	public void noChargeWhenUserSubscriptionStartedAfterTheBillingMonth() throws ParseException {
		User user = new User(1, "Employee", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 11, 10), customerId);
		User[] users = { user };
		assertEquals(0, Challenge.monthlyCharge("2018-10", plan, users));
	}

	@Test
	public void noChargeWhenUserSubscriptionEndedBeforeTheBillingMonth() throws ParseException {
		User user = new User(1, "Employee", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 11, 10), customerId);
		User[] users = { user };
		assertEquals(0, Challenge.monthlyCharge("2021-10", plan, users));
	}

	@Test
	public void chargeForTheFullBillingMonthWithEndDate() throws ParseException {
		User user = new User(1, "Employee", LocalDate.of(2019, 1, 1), LocalDate.of(2023, 11, 10), customerId);
		User[] users = { user };
		assertEquals(5000, Challenge.monthlyCharge("2021-10", plan, users));
	}
	
	@Test
	public void chargeForTheFullBillingMonthWithoutEndDate() throws ParseException {
		User user = new User(1, "Employee", LocalDate.of(2019, 1, 1), null, customerId);
		User[] users = { user };
		assertEquals(5000, Challenge.monthlyCharge("2021-10", plan, users));
	}
	
	@Test
	public void chargeForPartialBillingMonthWithoutEndDate() throws ParseException {
		User user = new User(1, "Employee", LocalDate.of(2021, 10, 5), null, customerId);
		User[] users = { user };
		assertEquals(4354, Challenge.monthlyCharge("2021-10", plan, users));
	}
}