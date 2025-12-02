from django.db import models
from patient.models import Patient

# Create your models here.
class HealthRecord(models.Model):

    record_date = models.DateField()
    preassure_systolic = models.IntegerField(null=True, blank=True)
    preassure_diastolic = models.IntegerField(null=True, blank=True)
    record_diagnosis = models.TextField(blank=True, null=True)
    
    # El campo se define aquí como 'patient'
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE)
    record_active = models.BooleanField(default=True)

    def __str__(self):
        # Corrección: Usar self.patient para acceder al objeto relacionado
        return f"Health Record - {self.patient.patient_first_name} {self.patient.patient_last_name} ({self.record_date})"

    class Meta:
        db_table = 'health_record'
        verbose_name = 'Health Record'
        verbose_name_plural = 'Health Records'
        ordering = ['-record_date']