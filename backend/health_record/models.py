from django.db import models
from patient.models import Patient
from django.conf import settings

# Create your models here.
class HealthRecord(models.Model):

    record_date = models.DateField()
    preassure_systolic = models.IntegerField(null=True, blank=True)
    preassure_diastolic = models.IntegerField(null=True, blank=True)

    medical_data = models.TextField()
    record_diagnosis = models.TextField(blank=True, null=True)
    
    # El campo se define aquí como 'patient'
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE)

    # --- Campos de Auditoría ---
    active = models.BooleanField(default=True, verbose_name="activo")
    created_by = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.SET_NULL, null=True, blank=True, related_name='+', verbose_name="creado por")
    created_at = models.DateTimeField(auto_now_add=True, verbose_name="fecha de creación")
    modified_by = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.SET_NULL, null=True, blank=True, related_name='+', verbose_name="modificado por")
    updated_at = models.DateTimeField(auto_now=True, verbose_name="última modificación")

    def __str__(self):
        # Corrección: Usar self.patient para acceder al objeto relacionado
        return f"Health Record - {self.patient.patient_first_name} {self.patient.patient_last_name} ({self.record_date})"

    class Meta:
        db_table = 'health_record'
        verbose_name = 'Health Record'
        verbose_name_plural = 'Health Records'
        ordering = ['-record_date']