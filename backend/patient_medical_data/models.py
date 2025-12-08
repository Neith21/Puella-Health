from django.db import models
from patient.models import Patient
from django.conf import settings

class PatientMedicalData(models.Model):
    BLOOD_TYPE_CHOICES = [
        ('A+', 'A+'),
        ('A-', 'A-'),
        ('B+', 'B+'),
        ('B-', 'B-'),
        ('AB+', 'AB+'),
        ('AB-', 'AB-'),
        ('O+', 'O+'),
        ('O-', 'O-'),
    ]
    
    patient_height_cm = models.DecimalField(max_digits=5, decimal_places=2, null=True, blank=True)
    patient_weight_kg = models.DecimalField(max_digits=5, decimal_places=2, null=True, blank=True)
    patient_blood_type = models.CharField(max_length=3, choices=BLOOD_TYPE_CHOICES, null=True, blank=True)
    patient_allergies = models.TextField(blank=True, null=True)
    patient_existing_conditions = models.TextField(blank=True, null=True)
    
    # El campo se llama 'patient'
    patient = models.ForeignKey(Patient, on_delete=models.CASCADE)

    # --- Campos de Auditoría ---
    active = models.BooleanField(default=True, verbose_name="activo")
    created_by = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.SET_NULL, null=True, blank=True, related_name='+', verbose_name="creado por")
    created_at = models.DateTimeField(auto_now_add=True, verbose_name="fecha de creación")
    modified_by = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.SET_NULL, null=True, blank=True, related_name='+', verbose_name="modificado por")
    updated_at = models.DateTimeField(auto_now=True, verbose_name="última modificación")

    def __str__(self):
        # Accedemos a self.patient
        return f"Medical Data - {self.patient.patient_first_name} {self.patient.patient_last_name}"

    class Meta:
        db_table = 'patient_medical_data'
        verbose_name = 'Patient Medical Data'
        verbose_name_plural = 'Patient Medical Data'