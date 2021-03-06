/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.meveo.model.AuditableEntity;

/**
 * @author Ignas Lelys
 * @created Nov 28, 2010
 * 
 */
@Entity
@Table(name = "CAT_DISCOUNT_PLAN_MATRIX", uniqueConstraints = { @UniqueConstraint(columnNames = { "EVENT_CODE",
        "START_SUBSCRIPTION_DATE", "END_SUBSCRIPTION_DATE", "PROVIDER_ID"}) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_DISCOUNT_PLAN_MATRIX_SEQ")
public class DiscountPlanMatrix extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "EVENT_CODE", length = 20, nullable = false)
    @Length(max = 20)
    @NotNull
    @NotEmpty
    private String eventCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_SUBSCRIPTION_DATE")
    private Date startSubscriptionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_SUBSCRIPTION_DATE")
    private Date endSubscriptionDate;

    @Column(name = "NB_PERIOD")
    private Integer nbPeriod;

    @Column(name = "PERCENT", precision = 19, scale = 8)
    @Digits(integer = 19, fraction = 8)
    @Min(0)
    private BigDecimal percent;

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Date getStartSubscriptionDate() {
        return startSubscriptionDate;
    }

    public void setStartSubscriptionDate(Date startSubscriptionDate) {
        this.startSubscriptionDate = startSubscriptionDate;
    }

    public Date getEndSubscriptionDate() {
        return endSubscriptionDate;
    }

    public void setEndSubscriptionDate(Date endSubscriptionDate) {
        this.endSubscriptionDate = endSubscriptionDate;
    }

    public Integer getNbPeriod() {
        return nbPeriod;
    }

    public void setNbPeriod(Integer nbPeriod) {
        this.nbPeriod = nbPeriod;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((eventCode == null) ? 0 : eventCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        DiscountPlanMatrix other = (DiscountPlanMatrix) obj;
        if (eventCode == null) {
            if (other.eventCode != null)
                return false;
        } else if (!eventCode.equals(other.eventCode))
            return false;
        return true;
    }

    @AssertTrue(message = "start date should be before end date")
    public boolean isValidDate() {
        return this.startSubscriptionDate.before(this.endSubscriptionDate);
    }

}
