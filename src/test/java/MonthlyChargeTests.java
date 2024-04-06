
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

public class MonthlyChargeTests {

	static final int subscriptionId = 1;
	static final int customerId = 1;
	static final int monthlyPriceInCents = 5000;

	Subscription plan = new Subscription(subscriptionId, customerId, monthlyPriceInCents);

	@Test
	public void noChargeWhenUserSubscriptionStartedAfterTheBillingMonth() {
		User user = new User(1, "Employee", LocalDate.of(2021, 11, 1), LocalDate.of(2023, 11, 10), customerId);
		User[] users = { user };
		assertEquals(0, Challenge.monthlyCharge("2021-10", plan, users));
	}

	@Test
	public void noChargeWhenUserSubscriptionEndedBeforeTheBillingMonth() {
		User user = new User(1, "Employee", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 11, 10), customerId);
		User[] users = { user };
		assertEquals(0, Challenge.monthlyCharge("2021-10", plan, users));
	}

	@Test
	public void chargeForTheFullBillingMonthWithEndDate() {
		User user = new User(1, "Employee", LocalDate.of(2019, 1, 1), LocalDate.of(2023, 11, 10), customerId);
		User[] users = { user };
		assertEquals(5000, Challenge.monthlyCharge("2021-10", plan, users));
	}

	@Test
	public void chargeForTheFullBillingMonthWithoutEndDate() {
		User user = new User(1, "Employee", LocalDate.of(2019, 1, 1), null, customerId);
		User[] users = { user };
		assertEquals(5000, Challenge.monthlyCharge("2021-10", plan, users));
	}

	@Test
	public void chargeForPartialBillingMonthWithoutEndDate() {
		User user = new User(1, "Employee", LocalDate.of(2021, 10, 5), null, customerId);
		User[] users = { user };
		// Charge for 27 days only
		assertEquals(5000 * 27 / 31, Challenge.monthlyCharge("2021-10", plan, users));
	}

	@Test
	public void chargeForPartialBillingMonthWithEndDate() {
		User user = new User(1, "Employee", LocalDate.of(2021, 10, 5), LocalDate.of(2023, 11, 10), customerId);
		User[] users = { user };
		// Charge for 27 days only
		assertEquals(5000 * 27 / 31, Challenge.monthlyCharge("2021-10", plan, users));
	}

	@Test
	public void chargeForThePartialBillingMonthWithoutStartDateWithEndDate() {
		User user = new User(1, "Employee", LocalDate.of(2021, 9, 1), LocalDate.of(2021, 10, 10), customerId);
		User[] users = { user };
		assertEquals(5000 * 10 / 31, Challenge.monthlyCharge("2021-10", plan, users));
	}

	@Test
	public void chargeForPartialBillingMonthWithStartAndEndDate() {
		User user = new User(1, "Employee", LocalDate.of(2021, 10, 5), LocalDate.of(2021, 10, 10), customerId);
		User[] users = { user };
		assertEquals(5000 * 7 / 31, Challenge.monthlyCharge("2021-10", plan, users));
	}

	@Test
	public void testAll() {
		User user1 = new User(1, "Employee #1", LocalDate.of(2021, 11, 1), LocalDate.of(2023, 11, 10), customerId);
		User user2 = new User(1, "Employee #2", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 11, 10), customerId);
		User user3 = new User(1, "Employee #3", LocalDate.of(2019, 1, 1), LocalDate.of(2023, 11, 10), customerId);
		User user4 = new User(1, "Employee #4", LocalDate.of(2019, 1, 1), null, customerId);
		User user5 = new User(1, "Employee #5", LocalDate.of(2021, 10, 5), null, customerId);
		User user6 = new User(1, "Employee #6", LocalDate.of(2021, 10, 5), LocalDate.of(2023, 11, 10), customerId);
		User user7 = new User(1, "Employee #7", LocalDate.of(2021, 9, 1), LocalDate.of(2021, 10, 10), customerId);
		User user8 = new User(1, "Employee #8", LocalDate.of(2021, 10, 5), LocalDate.of(2021, 10, 10), customerId);
		User[] users = { user1, user2, user3, user4, user5, user6, user7, user8 };
		assertEquals(5000 * (31 + 31 + 27 + 27 + 10 + 7) / 31, Challenge.monthlyCharge("2021-10", plan, users));
	}
}